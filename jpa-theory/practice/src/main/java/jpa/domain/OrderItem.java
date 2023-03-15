package jpa.domain;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import jpa.domain.common.BaseEntity;

@Entity
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @Column(name = "ORDER_PRICE")
    private Integer orderPrice;

    private Integer count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public Integer getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Integer orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
