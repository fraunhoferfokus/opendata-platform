/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.opencities.berlin.opendata.entity.service.impl;

import org.opencities.berlin.opendata.entity.model.BoxEntry;
import org.opencities.berlin.opendata.entity.service.base.BoxEntryLocalServiceBaseImpl;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the box entry local service.
 * 
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencities.berlin.opendata.entity.service.BoxEntryLocalService}
 * interface.
 * </p>
 * 
 * <p>
 * Never reference this interface directly. Always use
 * {@link org.opencities.berlin.opendata.entity.service.BoxEntryLocalServiceUtil}
 * to access the box entry local service.
 * </p>
 * 
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 * 
 * @author bdi
 * @see org.opencities.berlin.opendata.entity.service.base.BoxEntryLocalServiceBaseImpl
 * @see org.opencities.berlin.opendata.entity.service.BoxEntryLocalServiceUtil
 */
public class BoxEntryLocalServiceImpl extends BoxEntryLocalServiceBaseImpl {

	public BoxEntry createBoxEntry(String ckanId, String title, String url,
			int votes, double rating) throws SystemException {

		long id = CounterLocalServiceUtil.increment(BoxEntry.class.getName());
		BoxEntry boxEntry = boxEntryPersistence.create(id);
		boxEntry.setCkanId(ckanId);
		boxEntry.setTitle(title);
		boxEntry.setUrl(url);
		boxEntry.setVotes(votes);
		boxEntry.setRating(rating);

		boxEntryPersistence.update(boxEntry, false);

		return boxEntry;
	}
}