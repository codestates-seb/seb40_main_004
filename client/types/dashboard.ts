export interface UserDashboard {
  userId: number;
  email: string;
  nickname: string;
  jobType: string;
  grade: string;
  point: number;
  github: string | null;
  blog: string | null;
  infoMessage: string | null;
  rank: number;
  avatar: {
    avatarId: number;
    filename: string;
    remotePath: string;
  } | null;
  tags: { name: string; tag_Id: number; ranking: number }[] | [];
  reviewBadges: { name: string; badge_Id: number }[] | [];
  articles:
    | {
        articleId: number;
        category: string;
        title: string;
        clicks: number;
        likes: number;
        isClosed: boolean;
        tags: [
          {
            tagId: number;
            name: string;
          },
          {
            tagId: number;
            name: string;
          },
        ];
        commentCount: number;
        answerCount: number;
        createdAt: string;
        lastModifiedAt: string;
        userInfo: {
          userId: number;
          nickname: string;
          grade: string;
        };
        avatar: {
          avatarId: number;
          filename: string;
          remotePath: string;
        };
      }[]
    | []; // 수정 필요
  activities:
    | {
        articleCount: number;
        answerCount: number;
        commentCount: number;
        total: number;
        createdDate: string;
      }[]
    | []; // 수정 필요
  reviews:
    | {
        reviewId: number;
        content: string;
        createdAt: string;
        userInfo: {
          userId: number;
          nickname: string;
          grade: string;
        };
      }[]
    | []; // 수정 필요
}
