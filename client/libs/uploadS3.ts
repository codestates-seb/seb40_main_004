import axios from 'axios';
import { client } from './client';

export const getFileUrl = async () => {
  return await (
    await client.get('/api/files')
  ).data;
};

export const uploadImg = async (url: string, file: any) => {
  axios
    .put(url, file, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    .then((res) => {
      console.log(res);
    })
    .catch((err) => console.error(err));
};
