package com.inatel.ecommerce.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	 * @param cpf
	 * @param pedidoNumber
	 * @return
	 */
	public StatusDeEntrega startPaymentOfpedido(String cpf, int pedidoNumber) {
		
		if (cpf == null || pedidoNumber < 0) {
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.SEM_INFORMACAO, cpf, pedidoNumber);
		}
		Pedido pedido;
		try {
			pedido = clientPedido.recuperarPedido(pedidoNumber); //(1) consulta o pedido pelo número
		} catch(Exception e ) {
			System.out.println("pedido " + pedidoNumber + " not found.");
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.PEDIDO_NAO_ENCONTRADO,cpf, pedidoNumber);
		}
		
		if(pedido.getStatus() != Pedido.STATUS.PENDING.ordinal()) {
			System.out.println("Invalid pedido status: " + pedidoNumber + ": " + pedido.getStatus());
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.PEDIDO_STATUS_INVALIDO,cpf, pedidoNumber);
		}

		pedido.setIssueDate(new Date());
		pedido.setStatus(Pedido.STATUS.PENDING.ordinal()); //pendente de pagamento
		
		try {
			clientPedido.atualizarPedido(pedido); //(2) atualiza o status do pedido
		} catch(Exception e ) {
			System.out.println("Erro no serviço de pedido: update");
			return StatusDeEntrega.createErrorStatus(ENTREGA_STATUS.PEDIDO_ERRO, cpf, pedidoNumber);
		}
		
		try {
			clientEmail.callSendMailService(PDFContent); //(4) envia email com o pdf
		} catch(Exception e ) {
			System.out.println("Erro no serviço de email");
			return StatusDeEntrega.createErrorStatus(cpf, pedidoNumber, ENTREGA_STATUS.EMAIL_ERRO);
		}
		System.out.println("Sucesso ao inicializar o pagamento: pedidoNumber: " + pedidoNumber + " cpf: " + cpf);
		return new StatusDeEntrega(ENTREGA_STATUS.OK.ordinal(), cpf, pedidoNumber); //(5) retorna sucesso
	}

	/**
	 * Lógica de confirmação de pagamento
	 * (1) consulta o pedido pelo número
	 * (2) confirma o pagamento
	 * (3) atualiza o status do pedido
	 * (4) responde Ok
	 * 
	 * @param cpf
	 * @param pedidoNumber
	 * @return
	 */
	public StatusDeEntrega confirmPaymentOfpedido(String cpf, int pedidoNumber) {
		
		if (cpf == null || pedidoNumber < 0) {
			return StatusDeEntrega.createErrorStatus(cpf, pedidoNumber, ENTREGA_STATUS.SEM_INFORMACAO);
		}
		
		pedido pedido = clientPedido.retrievepedido(pedidoNumber); //(1) consulta o pedido pelo número

		if(pedido == null) { //alguma hora vai ser preciso verificar o status do pedido aqui
			System.out.println("Erro no serviço de pedido: pedido is null.");
			return StatusDeEntrega.createErrorStatus(cpf, pedidoNumber, ENTREGA_STATUS.PEDIDO_NAO_ENCONTRADO);
		}
		pedido.setPaymentDate(new Date());
		pedido.setStatus(pedido.STATUS.CONFIRMED.ordinal()); //(2) confirma o pagamento
		try {
			clientPedido.updatepedido(pedido); //(3) atualiza o status do pedido
		} catch(Exception e ) {
			System.out.println("Erro no serviço de pedido: update");
			return StatusDeEntrega.createErrorStatus(cpf, pedidoNumber, ENTREGA_STATUS.PEDIDO_ERRO);
		}
		System.out.println("Sucesso ao confirmar o pagamento: pedidoNumber: " + pedidoNumber + " cpf: " + cpf);
		return new StatusDeEntrega(ENTREGA_STATUS.OK.ordinal(), cpf, pedidoNumber); //(4) responde Ok
	}
}
