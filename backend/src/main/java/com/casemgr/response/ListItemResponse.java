package com.casemgr.response;

import java.io.Serializable;
import java.math.BigDecimal; // Added import for BigDecimal

import com.casemgr.entity.ListItem; // Added import

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ListItemResponse implements Serializable {

    private Long lId;
    private String name;
    private BigDecimal quantity; // Changed type to BigDecimal
    private BigDecimal unitPrice; // Changed type to BigDecimal
    private String unit;
    private Integer sort;
    private Boolean selected; // Key field indicating if this option is chosen
    private Integer blockSort;

    /**
     * Constructor to map from ListItem entity.
     * @param entity The ListItem entity to map from.
     */
//    public ListItemResponse(ListItem entity) {
//        if (entity != null) {
//            this.lId = entity.getLId();
//            this.name = entity.getName();
//            this.quantity = entity.getQuantity();
//            this.unitPrice = entity.getUnitPrice();
//            this.unit = entity.getUnit();
//            this.selected = entity.getSelected();
//            this.blockSort = entity.getBlockSort();
//        }
//    }
}