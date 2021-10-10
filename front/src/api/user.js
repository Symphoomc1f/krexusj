import request from '@/utils/request'

export function login(data) {
  return request({
    headers: {
      'app-id': '123'
    },
    url: '/api/user/login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/api/user/info',
    method: 'get',
    params: { token }
  })
}

export function changePwd(data) {
  return request({
    url: '/api/user/changePassword',
    method: 'post',
    data
  })
}

export function logout(token) {
  return request({
    url: '/api/user/logout',
    method: 'post',
    data: {
      token: token
    }
  })
}

export function getUsers(params) {
  return request({
      url: '/api/user/getUsers',
      method: 'get',
      params: {
          page: 1,
          row: 10,
          levelCd: '02'
      }
  })
}

export function getUsersByCondition(params) {
  return request({
      url: '/api/user/getUsers',
      method: 'get',
      params
  })
}

export function deleteUserControls(params) {
  return request({
      url: '/api/user/deleteUser',
      method: 'post',
      data:params
  })
}

export function updateUser(params) {
  return request({
      url: '/api/user/updateUser',
      method: 'post',
      data:params
  })
}

export function insertUser(params) {
  return request({
      url: '/api/user/insertUser',
      method: 'post',
      data:params
  })
}