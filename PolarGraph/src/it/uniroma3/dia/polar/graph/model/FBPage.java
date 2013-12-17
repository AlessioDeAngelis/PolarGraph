package it.uniroma3.dia.polar.graph.model;

import java.util.List;

import com.restfb.Facebook;
import com.restfb.types.CategorizedFacebookType;
import com.restfb.types.Page;

/**
 * An extension of the class page of the restfb package in order to take into
 * account even the category list since in the current restfb implementation it
 * is missing
 * */
public class FBPage extends Page {
	@Facebook("category_list")
	List<CategorizedFacebookType> categoryList;

	public List<CategorizedFacebookType> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<CategorizedFacebookType> categoryList) {
		this.categoryList = categoryList;
	}
}
