import Axios from 'axios';

const ISO_DATE_FORMAT_REGEX = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)(?:Z|([+-])([\d|:]*))?$/;

function dateJsonReviver(key: string, value: any) {
  if (typeof value === 'string' && ISO_DATE_FORMAT_REGEX.test(value)) {
    return new Date(value);
  }
  return value;
}

const AxiosInstance = Axios.create({
  baseURL: 'http://localhost:1111',
  transformResponse: (data) => {
    if (typeof data === 'string') {
      try {
        if (data !== '') data = JSON.parse(data, dateJsonReviver);
      } catch (e) {
        console.error(e);
      }
    }
    return data;
  },
});

export default AxiosInstance;
