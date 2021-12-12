package io.tonnyl.moka.common.ui

import androidx.paging.PagingConfig

private const val PER_PAGE = 16
private const val MAX_SIZE_OF_PAGED_LIST = 1024

val defaultPagingConfig = PagingConfig(
    pageSize = PER_PAGE,
    maxSize = MAX_SIZE_OF_PAGED_LIST,
    enablePlaceholders = true
)