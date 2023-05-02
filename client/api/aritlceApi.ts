import { client } from '@libs/client';
import { ReportProps } from '@type/article';

export const ArticleApi = {
  delete: (articleId: string) => client.delete(`/api/articles/${articleId}`),

  report: (articleId: string, payload: ReportProps) =>
    client.post(`/api/articles/${articleId}/reports`, payload),
};
