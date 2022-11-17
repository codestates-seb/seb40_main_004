/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-16
 * 개요:
   - /question/{article-id} 페이지에서 article-id 로 데이터를 요청하여 렌더링합니다.
   - 제목/작성자/작성시간/좋아요/북마크 컴포넌트
   - 질문 본문 
   - 태그 컴포넌트
   - 코멘트 컴포넌트
 */
import { QuestionTitle } from './QuestionTitle';
import { QuestionMainText } from './QuestionMainText';
import { CommentList } from '../Comment/CommentList';
import { TagList } from './TagList';
import { BtnLike } from '../BtnLike';
import { BtnBookmark } from '../BtnBookmark';
import { useFetch } from '../../../libs/useFetch';
import { ArticleDetail } from '../../../mocks/types';
import { UserNickname } from './UserNickname';
import { CreatedDate } from './CreatedDate';

export const QuestionContent = () => {
  const { data, isLoading } = useFetch('/api/articles/1');
  const content: ArticleDetail = data?.article;

  if (isLoading) return <div>로딩중~</div>;
  return (
    <main className="flex flex-col w-full pb-6 mb-16 border-b">
      <section className="flex flex-col space-y-4 border-b pb-3">
        <QuestionTitle title={content.title} />
        <div className="flex justify-between items-center">
          <section className="flex w-full justify-between">
            <article className="space-x-3 flex items-end">
              <UserNickname name={content.author} id={content.authorId} />
              <CreatedDate createdAt={content.createdAt} />
            </article>
          </section>

          <div className="flex space-x-1">
            <BtnLike />
            <span className="text-xl pr-3">14</span>
            <BtnBookmark />
          </div>
        </div>
      </section>

      <section className="p-6">
        <QuestionMainText content={content.content} />
        <div className="flex justify-between items-end space-y-3 sm:space-y-0 py-4 flex-col sm:flex-row">
          <TagList tags={content.tags} />
          <article className="space-x-2 text-sm w-[80px] flex justify-end">
            <button>수정</button>
            <button>삭제</button>
          </article>
        </div>
      </section>

      <section className="space-y-3 border-l pl-3">
        <h3 className="text-xl font-bold">{content.comments.length} 코멘트</h3>
        <CommentList />
      </section>
    </main>
  );
};
