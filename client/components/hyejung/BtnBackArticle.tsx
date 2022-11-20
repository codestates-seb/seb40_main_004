/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-20
 */
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';
import Link from 'next/link';

type BtnBackArticleProp = {
  articleId: number;
};

export const BtnBackArticle = ({ articleId = 1 }: BtnBackArticleProp) => {
  return (
    <Link href={`/questions/${articleId}`}>
      <button className="text-base md:text-lg font-bold">
        <FontAwesomeIcon icon={faChevronLeft} className="fa-lg mr-1" />
        질문으로 돌아가기
      </button>
    </Link>
  );
};
