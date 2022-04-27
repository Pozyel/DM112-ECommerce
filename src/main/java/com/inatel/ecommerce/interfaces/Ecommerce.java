package com.inatel.ecommerce.interfaces;

import java.util.List;

import com.inatel.ecommerce.model.Pedido;
import com.inatel.ecommerce.model.StatusDeEntrega;

public interface Ecommerce {
	
	StatusDeEntrega comecaEntregaDoPedido(Pedido pedido);

	StatusDeEntrega confirmaEntregaDoPedido(Pedido pedido);

    List<Pedido> listaDePedidosParaEntrega();
}
