import request from '@/utils/request'

export function getTasks(params) {
    return request({
        url: '/api/task/getTasks',
        method: 'get',
        params: {
            page: 1,
            row: 10
        }
    })
}

export function getTasksByCondition(params) {
    return request({
        url: '/api/task/getTasks',
        method: 'get',
        params
    })
}

export function getTaskTemplates(params) {
    return request({
        url: '/api/task/getTaskTemplates',
        method: 'get',
        params
    })
}

export function updateTask(params) {
    return request({
        url: '/api/task/updateTask',
        method: 'post',
        data:params
    })
}

export function deleteTask(params) {
    return request({
        url: '/api/task/deleteTask',
        method: 'post',
        data:params
    })
}

export function saveTask(params) {
    return request({
        url: '/api/task/saveTask',
        method: 'post',
        data:params
    })
}


export function startTask(params) {
    return request({
        url: '/api/task/startTask',
        method: 'post',
        data:params
    })
}

export function stopTask(params) {
    return request({
        url: '/api/task/stopTask',
        method: 'post',
        data:params
    })
}



