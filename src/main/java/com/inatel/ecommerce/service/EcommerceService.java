package com.inatel.ecommerce.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import com.inatel.ecommerce.cliente.ClienteEmail;
import com.inatel.ecommerce.cliente.ClientePedido;
import com.inatel.ecommerce.model.Pedido;
import com.inatel.ecommerce.model.StatusDeEntrega;
import com.inatel.ecommerce.model.StatusDeEntrega.ENTREGA_STATUS;

@Service
public class EcommerceService {
	@Autowired
	private ClientePedido clientPedido;
	
	@Autowired
	private ClienteEmail clientEmail;

	/**
	 * Lógica de geração de pendência de pagamento
	 * (1) consulta o pedido pelo número
	 * (2) atualiza o status do pedido
	 * (3) gera o boleto
	 * (4) envia email com o pdf
	 * (5) retorna sucesso
	 * 
	 * @param cpfReceiver
	 * @param pedidoNumber
	 * @return
	 */
	public StatusDeEntrega iniciaEntregaPedido( int pedidoNumber) {
		
		if ( pedidoNumber < 0) {
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.SEM_INFORMACAO, null, pedidoNumber);
		}
		Pedido pedido;
		try {
			pedido = clientPedido.recuperarPedido(pedidoNumber); //(1) consulta o pedido pelo número
		} catch(Exception e ) {
			System.out.println("pedido " + pedidoNumber + " not found.");
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.PEDIDO_NAO_ENCONTRADO, null, pedidoNumber);
		}
		
		if(pedido.getStatus() != Pedido.STATUS.CONFIRMED.ordinal()
				|| pedido.getDeliveryStatus() != Pedido.DELIVERY_STATUS.PENDING.ordinal()) {
			System.out.println("status do pedido inválido: " + pedidoNumber + ": " + pedido.getStatus());
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.PEDIDO_STATUS_INVALIDO, null, pedidoNumber);
		}

		pedido.setDeliveryStatus(Pedido.DELIVERY_STATUS.ONGOING.ordinal());
		
		try {
			clientPedido.atualizarPedido(pedido); //(2) atualiza o status do pedido
		} catch(Exception e ) {
			System.out.println("Erro no serviço de pedido: update");
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.PEDIDO_ERRO, null, pedidoNumber);
		}
		
		System.out.println("Sucesso ao inicializar entrega do pedido: pedidoNumber: " + pedidoNumber + " cpfReceiver: " + null);
		return new StatusDeEntrega(ENTREGA_STATUS.OK.toString(), null, pedidoNumber); //(5) retorna sucesso
	}

	/**
	 * Lógica de confirmação de pagamento
	 * (1) consulta o pedido pelo número
	 * (2) confirma o pagamento
	 * (3) atualiza o status do pedido
	 * (4) responde Ok
	 * 
	 * @param cpfReceiver
	 * @param pedidoNumber
	 * @return
	 */
	public StatusDeEntrega confirmarEntregaPedido(String cpfReceiver, int pedidoNumber) {
		
		if (cpfReceiver == null || pedidoNumber < 0) {
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.SEM_INFORMACAO, cpfReceiver, pedidoNumber);
		}
		
		Pedido pedido = clientPedido.recuperarPedido(pedidoNumber); //(1) consulta o pedido pelo número

		if(pedido == null) { //alguma hora vai ser preciso verificar o status do pedido aqui
			System.out.println("Erro no serviço de pedido: pedido não encontrado.");
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.PEDIDO_NAO_ENCONTRADO, cpfReceiver, pedidoNumber);
		}
		pedido.setDeliveryDate(new Date());
		pedido.setStatus(ENTREGA_STATUS.OK.ordinal()); //(2) confirma o recebimento
		try {
			clientPedido.atualizarPedido(pedido); //(3) atualiza o status do pedido
		} catch(Exception e ) {
			System.out.println("Erro no serviço de pedido: update");
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.PEDIDO_ERRO, cpfReceiver, pedidoNumber);
		}
		
		try {
            clientEmail.callSendMailService(null, "Confirmação de recebimento do pedido",
                    "Entregamos o seu pedido de numero " + pedidoNumber); // (5) envia email para o cliente
        } catch (Exception e) {
            System.out.println("Erro no serviço de email");
            return StatusDeEntrega.createErrorStatus(StatusDeEntrega.ENTREGA_STATUS.EMAIL_ERRO, cpfReceiver,
                    pedidoNumber);
        }
		
		System.out.println("Sucesso ao confirmar o recebimento: pedidoNumber: " + pedidoNumber + " cpfReceiver: " + cpfReceiver);
		return new StatusDeEntrega(ENTREGA_STATUS.OK.toString(), cpfReceiver, pedidoNumber); //(4) responde Ok
	}
	
	 /**
     * Lista todos os pedidos para entrega
     *
     * @return
     */
    public List<Pedido> getOrdersToDelivery() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("status", String.valueOf(Pedido.STATUS.CONFIRMED.ordinal()));
        queryParams.add("deliveryStatus", String.valueOf(Pedido.DELIVERY_STATUS.PENDING.ordinal()));

        return clientPedido.getOrders(queryParams);
    }
}
