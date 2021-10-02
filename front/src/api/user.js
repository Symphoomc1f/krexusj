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
