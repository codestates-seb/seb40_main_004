import axios from 'axios';

export const makePostRequest = async (endpoint: string, data: object) => {
  return await axios.post(endpoint, data);
};
