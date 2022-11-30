/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-23
 * 최근 수정일: 2022-11-26
 */
import Dompurify from 'dompurify';

type AnswerTextProps = {
  children: string;
};

// children은 html 형태의 string 으로 오기 때문에, 이를 변환하는 작업이 필요하다.
export const AnswerMainText = ({ children }: AnswerTextProps) => {
  return (
    <main>
      <div
        className="prose"
        dangerouslySetInnerHTML={{ __html: Dompurify.sanitize(children) }}
      />
    </main>
  );
};
