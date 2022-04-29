package com.inatel.ecommerce.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.inatel.ecommerce.interfaces.Ecommerce;
import com.inatel.ecommerce.model.Pedido;
import com.inatel.ecommerce.model.StatusDeEntrega;
import com.inatel.ecommerce.service.EcommerceService;

@RestController
@RequestMapping("/api")
public class EcommerceRest implements Ecommerce{
	
	@Autowired
	private EcommerceService service;
	
	@Override
    @PostMapping("/delivery/start")
	@ResponseStatus(HttpStatus.OK)
	public StatusDeEntrega comecaEntregaDoPedido(@RequestBody Pedido pedido) {
		return service.iniciaEntregaPedido( pedido.getNumber());
	}

	@Override
	@PostMapping("/delivery/confirm")
    @ResponseStatus(HttpStatus.OK)
	public StatusDeEntrega confirmaEntregaDoPedido(@RequestBody Pedido pedido) {
		return service.confirmarEntregaPedido(pedido.getDeliveryCpf(), pedido.getNumber());
	}

	@Override
    @GetMapping("/delivery/pedidos")
    @ResponseStatus(HttpStatus.OK)
	public List<Pedido> listaDePedidosParaEntrega() {
		return service.getOrdersToDelivery();
	}

}
