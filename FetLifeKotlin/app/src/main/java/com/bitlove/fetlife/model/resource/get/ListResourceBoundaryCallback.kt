package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList

class ListResourceBoundaryCallback<ResourceType>(val liveData: LiveData<PagedList<ResourceType>>) : PagedList.BoundaryCallback<ResourceType>(){


}