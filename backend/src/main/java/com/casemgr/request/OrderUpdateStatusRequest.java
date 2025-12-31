package com.casemgr.request;

import java.io.Serializable;

import com.casemgr.enumtype.OrderStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderUpdateStatusRequest implements Serializable {
	private OrderStatus orderStatus;
}