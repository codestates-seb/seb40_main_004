import { client } from '@libs/client';

export const ArticleApi = {
  delete: (articleId: string) => client.delete(`/api/articles/${articleId}`),
};
