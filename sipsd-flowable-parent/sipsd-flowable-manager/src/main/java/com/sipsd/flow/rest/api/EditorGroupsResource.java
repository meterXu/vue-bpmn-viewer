/* Licensed under the Apache License, Version 2.0 (the "License");
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
package com.sipsd.flow.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.ui.common.model.GroupRepresentation;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Rest resource for managing groups, used in the editor app.
 */
@Api(tags = { "工作流用户组管理API类" })
@RestController
@RequestMapping("/app")
public class EditorGroupsResource {

	@Autowired
	protected IdmIdentityService idmIdentityService;

	@RequestMapping(value = "/rest/editor-groups", method = RequestMethod.GET)
	@ApiOperation("查询用户组信息")
	public ResultListDataRepresentation getGroups(@RequestParam(required = false, value = "filter") String filter) {
		if (!StringUtils.isEmpty(filter)) {
			filter = filter.trim();
			 String sql = "select * from act_id_group where NAME_ like #{name}";
			//String sql = DBTypeContext.build().getRoleSql();
			filter = "%" + filter + "%";
			List<Group> groups = idmIdentityService.createNativeGroupQuery().sql(sql).parameter("name", filter).list();
			List<GroupRepresentation> result = new ArrayList<>();
			for (Group group : groups) {
				result.add(new GroupRepresentation(group));
			}
			return new ResultListDataRepresentation(result);
		}
		return null;
	}

	@RequestMapping(value = "/rest/group", method = RequestMethod.GET, produces = "application/json")
	public String getUserById(@RequestParam String groupId) {
		List<Group>  list = idmIdentityService.createNativeGroupQuery().sql("select * from act_id_group where ID_ = #{id} ").parameter("id", groupId).list();
		Group group = list.get(0);
		return group.getName();

	}
}
