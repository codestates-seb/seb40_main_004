// 백엔드 소통 O
export interface UserInfo {
  userId: number;
  nickname: string;
  grade: null;
}

// 백엔드 소통 O
export interface Avatar {
  avatarId: number;
  fileName: string;
  remotePath: string;
}

// 백엔드 소통 O
export interface Comment {
  commentId: number;
  articleId: number;
  content: string;
  createdAt: string;
  lastModifiedAt: string;
  userInfo: UserInfo;
  avatar: Avatar;
}

// 임의 지정
export interface Answer {
  id: string;
  author: string;
  authorId: string;
  level: string;
  createdAt: string;
  likes: number;
  profileImg: string;
  content: string;
  selection: boolean;
  isLiked: boolean;
  isBookmarked: boolean;
  comments: Comment[];
}

// 임의 지정
export interface ArticleDetail {
  id: string;
  title: string;
  author: string;
  authorId: string;
  level: string;
  createdAt: string;
  likes: number;
  content: string;
  tags: string[];
  selection: boolean;
  isLiked: boolean;
  isBookmarked: boolean;
  comments: Comment[];
}

// 임의 지정
export interface Articles {
  article: ArticleDetail;
  answers: Answer[];
}
