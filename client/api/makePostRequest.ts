import { client } from './../libs/client';

export const makePostRequest = async (endpoint: string, data: object) => {
  return await client.post(endpoint, data);
};
