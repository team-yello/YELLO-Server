package com.yello.server.domain.purchase.entity;

import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import com.yello.server.global.common.util.ConstantUtil;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@SuperBuilder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "transactionId_unique",
            columnNames = {"transactionId"}
        ),
    }
)
public class Purchase extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;
    
    @Column
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    @Convert(converter = GatewayConverter.class)
    private Gateway gateway;

    @Column(nullable = false)
    @Convert(converter = ProductTypeConverter.class)
    private ProductType productType;

    public static Purchase of(User user, ProductType productType, Gateway gateway,
        String transactionId) {
        return Purchase.builder()
            .price(setPrice(productType.toString()))
            .user(user)
            .gateway(gateway)
            .productType(productType)
            .transactionId(transactionId)
            .build();
    }

    public static int setPrice(String productType) {
        switch (productType) {
            case "YELLO_PLUS":
                return ConstantUtil.YELLO_PLUS;
            case "ONE_TICKET":
                return ConstantUtil.ONE_TICKET;
            case "TWO_TICKET":
                return ConstantUtil.TWO_TICKET;
            case "FIVE_TICKET":
                return ConstantUtil.FIVE_TICKET;
            default:
                return 0;
        }
    }

    public static Purchase createPurchase(User user, ProductType productType, Gateway gateway,
        String transactionId) {
        return Purchase.of(user, productType, gateway, transactionId);
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
