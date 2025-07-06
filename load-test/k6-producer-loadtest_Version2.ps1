import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100, // number of virtual users
    duration: '10s', // test duration
};

const BASE_URL = 'http://localhost:8084';
const JWT_TOKEN = 'eyJraWQiOiJIUGZNXC9paXhra1dFWlltTDFyVEtBRDgwMHI3UEU2VExKMmVSR3p4UTBcLzg9IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiI0a3ZhZm52MWFsMzVoYWs1Y280NW9sNnFqNyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiZGVmYXVsdC1tMm0tcmVzb3VyY2Utc2VydmVyLXA3ZWduelwvcmVhZCIsImF1dGhfdGltZSI6MTc1MTgyNTA5NSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LW5vcnRoLTEuYW1hem9uYXdzLmNvbVwvZXUtbm9ydGgtMV9mZzhRemhEa28iLCJleHAiOjE3NTE4Mjg2OTUsImlhdCI6MTc1MTgyNTA5NSwidmVyc2lvbiI6MiwianRpIjoiMjE2YWQ3YmQtNWQ3NC00NmM1LWExOTEtYTk4NTA4Zjg1NzJiIiwiY2xpZW50X2lkIjoiNGt2YWZudjFhbDM1aGFrNWNvNDVvbDZxajcifQ.So597zln74dUygZeh7TnzzmhdjCc37P2Tr2Svq7J5AkyGWy3ypaw10ebiFLwj0aqKAyuPjx2nbtxuhejwWO2xGu3j93ZVfwdkxh9lcNbCgS5ulvEWuZpuN32w5-XHTqyM7NpKVt-CgAP0Ag0DQ2OvBS8A1neJIk8H16xSgC-uQnmFfE-1xZ8Y6SbqAqkDlF3dsqoCzB5cih-gUE44tk8psK7CTXcGYK3JiPdzYslVnDkfs7UyQbDfjiyjyXNg2vjM1j09G2P9ejJFu9Neg9i82WLdHexBEOy90GNRYoJAVTGBu141QBnZW_bjcOZmev39iX2tz-rXYkgmJYBNsakSA';

export default function () {
    const url = `${BASE_URL}/api/data/ingest`;
    const payload = JSON.stringify({
        data: {
            field1: 'value1',
            field2: 'value2'
        }
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${JWT_TOKEN}`,
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

   
}