import { sleep } from 'k6';
import http from 'k6/http';

export const options = {
  duration: '60s',
  vus: 10,
  thresholds: {
    http_req_failed: ['rate<0.25'],
    http_req_duration: ['p(95)<1000'],
  },
};

export default function () {
  http.get('http://localhost:30000/first/ping');
  sleep(2);
}