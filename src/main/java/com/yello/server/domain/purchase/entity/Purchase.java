package com.yello.server.domain.purchase.entity;

import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import com.yello.server.global.common.util.ConstantUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
        @UniqueConstraint(
            name = "puchaseToken_unique",
            columnNames = {"purchaseToken"}
        ),
    }
)
public class Purchase extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String transactionId;

    @Column
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @Column(nullable = false)
    @Convert(converter = GatewayConverter.class)
    private Gateway gateway;

    @Column
    private String purchaseToken;

    @Column
    @Convert(converter = PurchaseStateConverter.class)
    private PurchaseState state;

    @Column(length = 15000)
    private String rawData;

    @Column(nullable = false)
    @Convert(converter = ProductTypeConverter.class)
    private ProductType productType;

    public static Purchase of(User user, ProductType productType, Gateway gateway,
        String transactionId, String purchaseToken, PurchaseState purchaseState, String rawData) {
        return Purchase.builder()
            .price(setPrice(productType.toString()))
            .user(user)
            .gateway(gateway)
            .purchaseToken(purchaseToken)
            .state(purchaseState)
            .rawData(rawData)
            .productType(productType)
            .transactionId(transactionId)
            .build();
    }

    public static int setPrice(String productType) {
        switch (productType) {
            case "YELLO_PLUS":
                return ConstantUtil.SALE_YELLO_PLUS;
            case "ONE_TICKET":
                return ConstantUtil.SALE_ONE_TICKET;
            case "TWO_TICKET":
                return ConstantUtil.SALE_TWO_TICKET;
            case "FIVE_TICKET":
                return ConstantUtil.SALE_FIVE_TICKET;
            default:
                return 0;
        }
    }

    public static Purchase createPurchase(User user, ProductType productType, Gateway gateway,
        String transactionId, String purchaseToken, PurchaseState purchaseState, String rawData) {
        return Purchase.of(user, productType, gateway, transactionId, purchaseToken, purchaseState, rawData);
    }

    public void setPurchaseState(PurchaseState state) {
        this.state = state;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
