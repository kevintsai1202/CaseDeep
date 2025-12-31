package com.casemgr.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.casemgr.entity.Account;
import com.casemgr.entity.Block;
import com.casemgr.enumtype.ContractStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {
    private Long cId;
    private String name;
    private String description;
    private BigDecimal contractPrice; // 合約金額
    private Boolean providerSigned;
    private String providerSignatureUrl;
    private Boolean clientSigned;
    private String clientSignatureUrl;
    private LocalDateTime revisionDate; // Last modification date of terms/signatures
    private ContractStatus contractStatus;
    private Account receivingAccount;
    private List<Block> blocks;
}