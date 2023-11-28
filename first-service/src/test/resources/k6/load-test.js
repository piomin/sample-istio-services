import { sleep } from 'k6';
import http from 'k6/http';

export const options = {
  duration: '20s',
  vus: 5,
  thresholds: {
    http_req_failed: ['rate<0.1'],
    http_req_duration: ['p(95)<500'],
  },
};

export default function () {
  http.get('http://localhost:30000/first/ping');
  sleep(3);
}