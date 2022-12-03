export interface userDashboard {
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

export interface articleList {
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
  ];
  commentCount: number;
  answerCount: number;
  createdAt: string;
  lastModifiedAt: string;
  userInfo: {
    userId: number;
    nickname: string;
    grade: string | null;
  };
  avatar: {
    avatarId: number | null;
    filename: string | null;
    remotePath: string | null;
  };
}

export interface rankList {
  userId: number;
  nickname: string;
  infoMessage: string | null;
  point: number;
  grade: string | null;
  jobType: string | null;
  articleCount: number;
  likeCount: number;
  answerCount: number;
  rank: number;
  avatar: {
    avatarId: number;
    filename: string;
    remotePath: string;
  } | null;
}

export interface IDataHeader {
  point: number;
  userInfo: {
    userId: number;
    nickname: string;
    grade: string;
  };
  avatar: {
    avatarId: number | null;
    filename: string | null;
    remotePath: string | null;
  } | null;
}
