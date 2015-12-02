package com.networks.utils;

import java.util.Collection;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;

public class CommonUtils {

	@SuppressWarnings("unchecked")
	public static <T> T findBeanObjectFromCollection(
			Collection<? extends T> collection, String propertyName,
			Object propertyValue) {
		T beanObjectToReturn = null;

		try {
			BeanPropertyValueEqualsPredicate equalsPredicate = new BeanPropertyValueEqualsPredicate(
					propertyName, propertyValue);
			
			Object objectFound = CollectionUtils.find(collection,
					equalsPredicate);
			if (objectFound != null) {
				beanObjectToReturn = (T) objectFound;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beanObjectToReturn;
	}

}
