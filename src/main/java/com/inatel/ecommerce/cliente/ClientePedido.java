package com.inatel.ecommerce.cliente;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.inatel.ecommerce.model.Pedido;
import reactor.core.publisher.Mono;

public class ClientePedido {
	@Value("${order.rest.url}")
	private String restURL;
	
	private final String endpoint = "/orders";

	/**
	 * criarPedido
	 * @param pedido
	 * @return 
	 */
	public void criarPedido(Pedido pedido) {

		String url = restURL + endpoint;
		System.out.println("URL: " + url);
		
		WebClient.create(url)
		        .post()
		        .contentType(MediaType.APPLICATION_JSON)
		        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
		        .body(Mono.just(pedido), Pedido.class)
		        .accept(MediaType.APPLICATION_JSON)
		        .retrieve();
		        //.log()

		System.out.println("Sucesso no createOrder para o pedido: " + pedido.getNumber());
	}
	
	/**
	 * getItems
	 * @param cpf
	 * @return List of orders
	 */
	public List<Pedido> getPedidosPorCPF(String cpf) {
		String url = restURL + endpoint + "/customer/" + cpf;
		System.out.println("URL: " + url);
		
		return WebClient.create(url)
		        .get()
		        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
		        .retrieve()
		        .bodyToFlux(Pedido.class)
		        .collectList()
		        .log()
		        .block();
	}
	
	/**
	 * retrieveOrder
	 * @param orderNumber
	 * @return
	 */
	public Pedido recuperarPedido(int pedidoNumero) {
		String url = restURL + endpoint + "/" + pedidoNumero;
		System.out.println("URL: " + url);
		
		return WebClient.create(url)
		        .get()
		        .retrieve()
		        .bodyToMono(Pedido.class)
		        .block();
	}
	
	/**
	 * updateOrder
	 * @param order
	 * @return
	 */
	public void atualizarPedido(Pedido pedido) {

		String url = restURL + endpoint + "/" + pedido.getNumber();
		System.out.println("URL: " + url);
		
		WebClient.create(url)
		        .put()
		        .contentType(MediaType.APPLICATION_JSON)
		        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
		        .body(Mono.just(pedido), Pedido.class)
		        .accept(MediaType.APPLICATION_JSON)
		        .retrieve();

		System.out.println("Sucesso no updateOrder para o pedido: " + pedido.getNumber());
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	
	public void setRestURL(String restURL) {
		this.restURL = restURL;
	}
}
