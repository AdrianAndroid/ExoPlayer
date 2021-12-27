/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.upstream;

/**
 * A source of allocations. 分配的来源
 */
public interface Allocator {

    /**
     * Obtain an {@link Allocation}. 获得分配
     *
     * <p>When the caller has finished with the {@link Allocation}, it should be returned by calling
     * {@link #release(Allocation)}.  当调用者完成分配时，它应该通过调用release返回。
     *
     * @return The {@link Allocation}.
     */
    Allocation allocate();

    /**
     * Releases an {@link Allocation} back to the allocator.
     *
     * @param allocation The {@link Allocation} being released.
     */
    void release(Allocation allocation);

    /**
     * Releases an array of {@link Allocation}s back to the allocator.
     *
     * @param allocations The array of {@link Allocation}s being released.
     */
    void release(Allocation[] allocations);

    /**
     * Hints to the allocator that it should make a best effort to release any excess {@link
     * Allocation}s.提示分配器它应该尽最大努力释放任何多余的分配。
     */
    void trim();

    /**
     * Returns the total number of bytes currently allocated. 返回当前分配的总字节数。
     */
    int getTotalBytesAllocated();

    /**
     * Returns the length of each individual {@link Allocation}. 返回每个单独分配的长度。
     */
    int getIndividualAllocationLength();
}
