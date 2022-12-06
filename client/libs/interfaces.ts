export interface Tags {
  tagId: number;
  name: string;
}

export interface UserInfo {
  userId: number;
  nickname: string;
  grade: string;
}

export interface Avatar {
  avatarId: number;
  fileName: string;
  remotePath: string;
}

export interface CommentResp {
  answerId?: number | null;
  commentId: number;
  articleId: number;
  content: string;
  createdAt: string;
  lastModifiedAt: string;
  userInfo: UserInfo;
  avatar: Avatar;
}

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

export interface Answer {
  answerId: number;
  authorId: string;
  content: string;
  createdAt: string;
  isLiked: boolean;
  isPicked: boolean;
  answerLikeCount: number;
  commentCount: number;
  commentPreview: CommentResp;
  avatar: Avatar;
  userInfo: UserInfo;
}

export interface PageInfo {
  page: number;
  size: number;
  totalElements: number;
  sort: { empty: boolean; unsorted: boolean; sorted: boolean };
}

export interface AnswerListProps {
  data: Answer[];
  pageInfo: PageInfo;
}

// 임의 지정
export interface Articles {
  article: ArticleDetail;
  answers: Answer[];
}

export interface QuestionListProps {
  answerCount: number;
  articleId: number;
  category: string;
  clicks: number;
  createdAt: string;
  isClosed: boolean;
  lastModifiedAt: string;
  likes: number;
  title: string;
  commentCount: number;
  avatar: Avatar;
  tags: Tags[];
  userInfo: UserInfo;
}

export interface AuthProps {
  email: string;
  authKey?: string;
}

export interface TitleProps {
  title: string;
}
