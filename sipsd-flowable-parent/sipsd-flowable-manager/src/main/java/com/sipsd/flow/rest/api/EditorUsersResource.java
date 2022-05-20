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

import cn.hutool.core.lang.UUID;
import com.codingapi.txlcn.tc.annotation.DTXPropagation;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sipsd.cloud.common.core.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.flowable.engine.ManagementService;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.ui.common.security.DefaultPrivileges;
import org.flowable.ui.common.service.exception.UnauthorizedException;
import org.flowable.ui.idm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = { "工作流用户管理API类" })
@RestController
@RequestMapping("/app")
public class EditorUsersResource {

	@Autowired
	protected IdmIdentityService idmIdentityService;
	@Autowired
	protected ManagementService managementService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	/**
	 * GET /rest/authenticate -> check if the user is authenticated, and return its
	 * full name.
	 */
	@RequestMapping(value = "/rest/authenticate", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation("用户授权信息")
	public ObjectNode isAuthenticated(HttpServletRequest request) {
		String user = request.getRemoteUser();

		if (user == null) {
			throw new UnauthorizedException("Request did not contain valid authorization");
		}
		ObjectNode result = objectMapper.createObjectNode();
		result.put("login", user);
		return result;
	}

	@RequestMapping(value = "/rest/editor-users", method = RequestMethod.GET)
	@ApiOperation("用户信息查询")
	public ResultListDataRepresentation getUsers(@RequestParam(value = "filter", required = false) String filter) {
		if (!StringUtils.isEmpty(filter)) {
			filter = filter.trim();
			// String sql = "select * from act_id_user where ID_ like #{id} or LAST_ like
			// #{name} limit 10";
			//String sql = DBTypeContext.build().getUserSql();
            String sql = "select * from act_id_user where ID_ like #{id} or LAST_ like #{name}";
			filter = "%" + filter + "%";
//            List<User> matchingUsers = idmIdentityService.createNativeUserQuery().sql(sql).parameter("username",filter).parameter("real_name",filter).list();
			List<User> matchingUsers = idmIdentityService.createNativeUserQuery().sql(sql).parameter("id", filter)
					.parameter("name", filter).list();
			List<UserRepresentation> userRepresentations = new ArrayList<>(matchingUsers.size());
			for (User user : matchingUsers) {
				userRepresentations.add(new UserRepresentation(user));
			}
			return new ResultListDataRepresentation(userRepresentations);
		}
		return null;
	}

	@RequestMapping(value = "/rest/account", method = RequestMethod.GET, produces = "application/json")
	public UserRepresentation getAccount() {
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setId("admin");
		userRepresentation.setEmail("admin@flowable.org");
		userRepresentation.setFullName("Test Administrator");
		userRepresentation.setLastName("Administrator");
		userRepresentation.setFirstName("Test");
		List<String> privileges = new ArrayList<>();
		privileges.add(DefaultPrivileges.ACCESS_MODELER);
		privileges.add(DefaultPrivileges.ACCESS_IDM);
		privileges.add(DefaultPrivileges.ACCESS_ADMIN);
		privileges.add(DefaultPrivileges.ACCESS_TASK);
		privileges.add(DefaultPrivileges.ACCESS_REST_API);
		userRepresentation.setPrivileges(privileges);
		return userRepresentation;
	}

	/**
	 * 添加用户
	 * @param user  用户
	 * @return
	 */
	@ApiOperation("插入用户")
	@PostMapping("/rest/saveUser")
	public Result<String> saveUser(UserEntityImpl user) {
		Result result = Result.sucess("添加成功");
		userService.createNewUser(UUID.randomUUID().toString(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getPassword(),"flow");
		return result;
	}

	/**
	 * 根据userId更新用户
	 * @param user  用户
	 * @return
	 */
	@ApiOperation("根据userId更新用户")
	@PostMapping("/rest/updateUser")
	public Result<String> updateUser(UserEntityImpl user) {
		Result result = Result.sucess("更新成功");
		userService.updateUserDetails(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail(),"flow");
		return result;
	}

	/**
	 * 根据userId删除用户
	 * @param userId  用户id
	 * @return
	 */
	@ApiOperation("根据userId删除用户")
	@PostMapping("/rest/deleteUser")
	public Result<String> updateUser(@RequestParam("userId") String userId) {
		Result result = Result.sucess( "删除成功");
		userService.deleteUser(userId);
		return result;
	}

	@GetMapping("/rest/saveUser2")
	@LcnTransaction(propagation = DTXPropagation.SUPPORTS)
	@Transactional
	public String saveUser() {
		userService.createNewUser(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),"flow");
		return "111";
	}

}
