import { rest } from 'msw';
import { Articles } from './types';

export const handlers = [
  rest.get('https://my.backend/api/articles/1', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json<Articles[]>([
        {
          article: {
            id: '1',
            title: '모킹 테스트중',
            author: '박혜정',
            authorId: '1',
            level: '3',
            createdAt: '2022년 11월 16일 오후 12시 49분',
            likes: 3,
            content:
              '본문도 테스트중이라 일단 string으로 보내본다... 나중에 코드블럭이랑 이미지는 어떻게 처리할건지 생각해봐야함. ㅜㅜ',
            tags: ['Next.js', 'Typescript', 'SWR'],
            selection: false,
            isLiked: false,
            isBookmarked: false,
            comments: [
              {
                commentId: 41,
                articleId: 64,
                content: 'test_359e2304ed58',
                createdAt: '2023-04-19 04:59:38',
                lastModifiedAt: '2019-01-24 05:01:53',
                userInfo: {
                  userId: 37,
                  nickname: 'test_616e077dd633',
                  grade: null,
                },
                avatar: {
                  avatarId: 37,
                  fileName: 'test_f6deab56ee15',
                  remotePath:
                    'https://images.unsplash.com/photo-1533738363-b7f9aef128ce?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=735&q=80',
                },
              },
            ],
          },
          answers: [
            {
              id: '1',
              author: '박혜정',
              authorId: '1',
              level: '3',
              createdAt: '2022년 11월 16일 오후 12시 49분',
              profileImg:
                'https://images.unsplash.com/photo-1518020382113-a7e8fc38eac9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=717&q=80',
              likes: 3,
              content:
                '본문도 테스트중이라 일단 string으로 보내본다... 나중에 코드블럭이랑 이미지는 어떻게 처리할건지 생각해봐야함. ㅜㅜ',
              selection: false,
              isLiked: false,
              isBookmarked: false,
              comments: [
                {
                  commentId: 41,
                  articleId: 64,
                  content: 'test_359e2304ed58',
                  createdAt: '2023-04-19 04:59:38',
                  lastModifiedAt: '2019-01-24 05:01:53',
                  userInfo: {
                    userId: 37,
                    nickname: 'test_616e077dd633',
                    grade: null,
                  },
                  avatar: {
                    avatarId: 37,
                    fileName: 'test_f6deab56ee15',
                    remotePath:
                      'https://images.unsplash.com/photo-1543852786-1cf6624b9987?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80',
                  },
                },
              ],
            },
          ],
        },
      ]),
    );
  }),

  rest.post('https://my.backend/api/articles/1/comments', (req, res, ctx) => {
    return res(ctx.status(200));
  }),
];
