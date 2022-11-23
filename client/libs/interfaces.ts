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

// 백엔드 소통 O but 확인 필요
export interface Answer {
  answerId: number;
  authorId: string;
  content: string;
  createdAt: string;
  isPicked: boolean;
  answerLikeCount: number;
  commentCount: number;
  commentPreview: CommentProps;
  avatar: Avatar;
  userInfo: UserInfo;
}

// 임의 지정
export interface Articles {
  article: ArticleDetail;
  answers: Answer[];
}
