import { Base } from './base';
// import { UserInfo } from './user';
// import { Avatar } from './user';

export interface Tags {
  tagId: number;
  name: string;
}

export interface ArticleDetail extends Base {
  isLiked: boolean;
  isBookmarked: boolean;
  expiredDate: null;
  comments: Comment[];
}

export interface ArticleListProps extends Base {
  answerCount: number;
  commentCount: number;
}

export interface ReportProps {
  reason: string;
  content: string;
}
