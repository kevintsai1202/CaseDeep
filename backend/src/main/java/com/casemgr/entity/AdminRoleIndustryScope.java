package com.casemgr.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理員角色產業範圍實體類別
 * 用於儲存管理員在特定角色下的產業管理範圍
 */
@Entity
@Table(name = "T_ADMIN_ROLE_INDUSTRY_SCOPE")
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminRoleIndustryScope extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * 關聯的用戶實體
     * 使用 @ManyToOne 註解表示多個 AdminRoleIndustryScope 實體可以對應一個 User 實體。
     * @JoinColumn 註解指定了外鍵欄位名稱為 "USER_ID"，並參考 User 實體的主鍵 "ID"。
     */
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false, referencedColumnName = "ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "INDUSTRY_ID")
    private Industry industry;

    @Column(name = "IS_ALL_INDUSTRIES", nullable = false)
    private Boolean isAllIndustries = false;
}