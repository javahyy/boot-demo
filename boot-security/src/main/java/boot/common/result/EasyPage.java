/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.dreamlu.net).
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

package boot.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * EasyUI Page返回
 */
@Getter
@Setter
@NoArgsConstructor
public class EasyPage<T> {

	private long total;
	private List<T> rows;

	private EasyPage(IPage<T> page) {
		this.rows = page.getRecords();
		this.total = page.getTotal();
	}

	public static <T> EasyPage<T> of(IPage<T> page) {
		return new EasyPage<>(page);
	}

}
