package com.casemgr.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.casemgr.converter.ListItemConverter;
import com.casemgr.entity.Block;
import com.casemgr.entity.Filedata;
import com.casemgr.entity.ListItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockResponse implements Serializable {
	private Long bId;
	private String name;
	private Integer sort;
	private String type;//list / file / text
	private Boolean multiple;
	private List<ListItemResponse> listItems;
//	private List<ListItemResponse> selectedListItems; 
	private Filedata fileData;
	private String context;
	
	public BlockResponse(Block block) {
		this.bId = block.getBId();
		this.name = block.getName();
		this.sort = block.getSort();
		this.type = block.getType().name();
		this.multiple = block.getMultiple();
		this.listItems = ListItemConverter.INSTANCE.entityToItemResponse(block.getListItems());
//		this.selectedListItems = block.getSelectedListItems().stream().map(ListItemResponse::new).toList();
//		if (block.getListItems()!=null) {
//			List<ListItemResponse> allListItemResponse = new ArrayList<>();
//			for (ListItem listItem: block.getListItems()) {
//				allListItemResponse.add(new ListItemResponse(listItem));
//			}
//			this.listItems = allListItemResponse;
////			this.listItems = ListItemConverter.INSTANCE.entityToResponse(block.getListItems());
//		}
//		this.fileData = block.getFileData();
		this.context = block.getContext();
	}
}