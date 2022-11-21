// 백엔드 소통 O
export interface Tags {
  tagId: number;
  name: string;
}

// 백엔드 소통 O
export interface UserInfo {
  userId: number;
  nickname: string;
  grade: string;
}

// 백엔드 소통 O
export interface Avatar {
  avatarId: number;
  fileName: string;
  remotePath: string;
}

// 백엔드 소통 O
export interface CommentProps {
  commentId: number;
  articleId: number;
  content: string;
  createdAt: string;
  lastModifiedAt: string;
  userInfo: UserInfo;
  avatar: Avatar;
}

// 백엔드 소통 O
export interface ArticleDetail {
  articleId: number;
  category: string;
  title: string;
  content: string;
  clicks: number;
  likes: number;
  isClosed: boolean;
  isLiked: boolean;
  isBookmarked: boolean;
  createdAt: string;
  lastModifiedAt: string;
  expiredDate: null;
  tags: Tags[];
  userInfo: UserInfo;
  avatar: Avatar;
  comments: Comment[];
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
export interface Articles {
  article: ArticleDetail;
  answers: Answer[];
}
