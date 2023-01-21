import { UserInfo } from './user';
import { Avatar } from './user';

export interface Tags {
  tagId: number;
  name: string;
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

export interface ArticleListProps {
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
