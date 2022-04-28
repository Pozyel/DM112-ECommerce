package com.inatel.ecommerce.model;

public class StatusDeEntrega {
	public enum ENTREGA_STATUS {
        OK, SEM_INFORMACAO, PEDIDO_NAO_ENCONTRADO, PEDIDO_STATUS_INVALIDO, PEDIDO_ERRO, EMAIL_ERRO
    }

    private String deliveryCpf;
    private int orderNumber;
    private String status;

    public StatusDeEntrega() {
    }

    public StatusDeEntrega(String status, String deliveryCpf, int orderNumber) {
        super();
        this.status = status;
        this.deliveryCpf = deliveryCpf;
        this.orderNumber = orderNumber;
    }

    public static StatusDeEntrega createErrorStatus(ENTREGA_STATUS errorStatus, String deliveryCpf, int orderNumber) {
        return new StatusDeEntrega(errorStatus.toString(), deliveryCpf, orderNumber);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryCpf() {
        return deliveryCpf;
    }

    public void setDeliveryCpf(String deliveryCpf) {
        this.deliveryCpf = deliveryCpf;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "PaymentStatus [deliveryCpf=" + deliveryCpf + ", orderNumber=" + orderNumber + ", status=" + status
                + "]";
    }
}
