/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-29
 */
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';
import Link from 'next/link';
import { useRecoilValue } from 'recoil';
import { reviewRequestAtom } from '../../atomsHJ';

type BtnBackArticleProp = {
  articleId: string;
};

export const BtnBackArticle = ({ articleId }: BtnBackArticleProp) => {
  const reviewRequest = useRecoilValue(reviewRequestAtom);
  if (reviewRequest.dashboardUrl)
    return (
      <Link href={`${reviewRequest.dashboardUrl}`}>
        <button className="text-base md:text-lg font-bold">
          <FontAwesomeIcon icon={faChevronLeft} className="fa-lg mr-1" />
          대시보드로 돌아가기
        </button>
      </Link>
    );
  else
    return (
      <Link href={`/questions/${encodeURIComponent(articleId)}`}>
        <button className="text-base md:text-lg font-bold">
          <FontAwesomeIcon icon={faChevronLeft} className="fa-lg mr-1" />
          질문으로 돌아가기
        </button>
      </Link>
    );
};
