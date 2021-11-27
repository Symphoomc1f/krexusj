import request from '@/utils/request'

export function getAttendances(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            machineTypeCd: '9997'
        }
    })
}

export function getAttendancesByCondition(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params
    })
}

export function deleteAttendances(params) {
    return request({
        url: '/api/machine/deleteMachine',
        method: 'post',
        data:params
    })
}

export function restartAttendances(params) {
    return request({
        url: '/api/machine/startMachine',
        method: 'post',
        data:params
    })
}


export function getAttendanceFace(params) {
    return request({
        url: '/api/machine/getMachineFaces',
        method: 'get',
        params
    })
}

export function getClasses(params) {
    return request({
        url: '/api/attendance/getClasses',
        method: 'get',
        params
    })
}

export function getDepartment(params) {
    return request({
        url: '/api/attendance/getDepartments',
        method: 'get',
        params
    })
}

export function getStaffs(params) {
    return request({
        url: '/api/attendance/getStaffs',
        method: 'get',
        params
    })
}


export function saveClassesStaffs(params) {
    return request({
        url: '/api/attendance/saveClassesStaffs',
        method: 'post',
        data:params
    })
}

export function getClassesStaffs(params) {
    return request({
        url: '/api/attendance/getClassesStaffs',
        method: 'get',
        params
    })
}

export function deleteClassesStaff(params) {
    return request({
        url: '/api/attendance/deleteClassesStaff',
        method: 'post',
        data:params
    })
}


export function getAttendanceTasks(params) {
    return request({
        url: '/api/attendance/getAttendanceTasks',
        method: 'get',
        params
    })
}

export function getMonthAttendance(params) {
    return request({
        url: '/api/attendance/getMonthAttendance',
        method: 'get',
        params
    })
}

export function getStaffAttendanceLog(params) {
    return request({
        url: '/api/attendance/getStaffAttendanceLog',
        method: 'get',
        params
    })
}






