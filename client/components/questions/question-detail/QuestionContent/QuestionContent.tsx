import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useRecoilValue, useSetRecoilState } from 'recoil';

import { BtnBookmark } from './BtnBookmark';
import { QuestionMainText } from './QuestionMainText';
import { CommentContainer } from '../Comment/index';

import { isArticleEditAtom } from '@atoms/articleAtom';
import { isLoginAtom } from '@atoms/loginAtom';

import { UserNickname } from '@components/common/UserNickname';
import { CreatedDate } from '@components/common/CreatedDate';
import { BtnLike } from '@components/common/BtnLike';
import { TagList } from '@components/common/TagList';

import { ArticleDetail } from '@type/article';

import { client } from '@libs/client';

type QuestionContentProps = {
  articleId: string;
  article: ArticleDetail;
};

export const QuestionContent = ({
  articleId,
  article,
}: QuestionContentProps) => {
  const router = useRouter();
  const isLogin = useRecoilValue(isLoginAtom);
  const setArticleEdit = useSetRecoilState(isArticleEditAtom);

  // 좋아요, 북마크 여부 확인을 위한 데이터 요청
  const [articleData, setArticleData] = useState<ArticleDetail>();
  useEffect(() => {
    async function getLikeBookmark() {
      const data = await client.get(`/api/articles/${articleId}`);
      setArticleData(data.data);
    }
    getLikeBookmark();
  }, []);

  let currUserId: any = '';
  if (typeof window !== 'undefined') {
    currUserId = localStorage.getItem('userId');
  }
  const authorId = article.userInfo.userId;
  const isClosed = article.isClosed;

  const onDelete = () => {
    if (confirm('게시글을 삭제하시겠습니까...?')) {
      client
        .delete(`/api/articles/${articleId}`)
        .then(() => {
          alert('게시글이 삭제되었습니다. 게시글 목록으로 돌아갑니다.');
          router.replace(`/questions`);
        })
        .catch((err) => {
          console.log(err);
          alert('답변 삭제에 실패했습니다.');
        });
    }
  };

  const onEdit = () => {
    setArticleEdit({
      isArticleEdit: true,
      title: article.title,
      content: article.content,
      articleId: articleId as string,
    });
    router.push(`/post`);
  };

  return (
    <main className="flex flex-col w-full pb-6 border-b">
      <>
        <section className="flex flex-col space-y-4 border-b pb-3">
          <section className="flex w-full text-2xl sm:text-3xl space-x-2">
            {article.category === 'QNA' && (
              <h1 className="text-main-yellow font-semibold">Q.</h1>
            )}
            <h1>{article.title}</h1>
          </section>
          <div className="flex justify-between items-center">
            <section className="flex w-full justify-between">
              <article className="space-x-3 flex items-end">
                <UserNickname
                  nickname={article.userInfo.nickname}
                  userId={article.userInfo.userId}
                  grade={article.userInfo.grade}
                />
                <CreatedDate createdAt={article.createdAt} />
              </article>
            </section>
            <div className="flex space-x-1">
              {articleData && (
                <>
                  <BtnLike
                    isLiked={articleData.isLiked}
                    likes={article.likes}
                  />
                  <BtnBookmark isBookmarked={articleData.isBookmarked} />
                </>
              )}
            </div>
          </div>
        </section>

        <section className="p-6">
          <QuestionMainText>{article.content}</QuestionMainText>
          <div className="flex justify-between items-end space-y-3 sm:space-y-0 py-4 flex-col sm:flex-row">
            <TagList tags={article.tags} />

            {isLogin && !isClosed && authorId.toString() === currUserId && (
              <article className="space-x-2 text-sm w-[80px] flex justify-end">
                <button onClick={onEdit}>수정</button>
                <button onClick={onDelete}>삭제</button>
              </article>
            )}
          </div>
        </section>

        <section className="space-y-3 border-l pl-4">
          <CommentContainer />
        </section>
      </>
    </main>
  );
};
