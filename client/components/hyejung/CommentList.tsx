/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-28
 */

import { useRouter } from 'next/router';
import { Comment } from './Comment';
import { CommentTextArea } from './CommentTextArea';
import { CommentResp } from '../../libs/interfaces';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';
import { useFetch } from '../../libs/useFetchSWR';
import { useRecoilState } from 'recoil';
import { isCommentOpenAtom } from '../../atomsHJ';

type CommentList = {
  comments: CommentResp[];
  setIsOpen: any;
  isOpen: boolean;
};

const CommentList = ({ comments, setIsOpen, isOpen }: CommentList) => {
  return isOpen ? (
    <>
      {comments.map((comment: CommentResp) => (
        <Comment
          key={comment.commentId}
          commentId={comment.commentId}
          articleId={comment.articleId}
          content={comment.content}
          createdAt={comment.createdAt}
          lastModifiedAt={comment.lastModifiedAt}
          userInfo={comment.userInfo}
          avatar={comment.avatar}
        />
      ))}
      <CommentTextArea />
      <button onClick={() => setIsOpen(false)}>
        {`닫기 `}
        <FontAwesomeIcon icon={faChevronUp} />
      </button>
    </>
  ) : (
    <>
      <Comment
        key={comments[0].commentId}
        commentId={comments[0].commentId}
        articleId={comments[0].articleId}
        content={comments[0].content}
        createdAt={comments[0].createdAt}
        lastModifiedAt={comments[0].lastModifiedAt}
        userInfo={comments[0].userInfo}
        avatar={comments[0].avatar}
      />
      <button onClick={() => setIsOpen(true)}>
        {`펼치기 `}
        <FontAwesomeIcon icon={faChevronDown} />
      </button>
    </>
  );
};

type CommentContainerProps = {
  answerId?: number;
};

export const CommentContainer = ({ answerId }: CommentContainerProps) => {
  const [isOpen, setIsOpen] = useRecoilState(isCommentOpenAtom);
  // 현재 게시글 id
  const router = useRouter();
  const { articleId } = router.query;

  // 답변 코멘트 || 질문 코멘트에 따른 요청 url
  const url = answerId
    ? `/api/answers/${answerId}/comments`
    : `/api/articles/${articleId}/comments`;

  // 코멘트 조회 요청 로직
  let comments;
  if (!answerId) {
    comments = useFetch(url).data;
  } else {
    comments;
  }
  if (!comments) comments = [];

  // 코멘트가 존재할때만 펼치기 기능이 있는 코멘트 리스트가 나옴!
  return (
    <>
      <h3 className="text-xl font-bold">{comments.length} 코멘트</h3>
      <div className="space-y-8 pt-4">
        {comments.length ? (
          <CommentList
            comments={comments}
            setIsOpen={setIsOpen}
            isOpen={isOpen}
          />
        ) : (
          <div>
            <CommentTextArea />
          </div>
        )}
      </div>
    </>
  );
};
