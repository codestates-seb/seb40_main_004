import Dompurify from 'dompurify';

type AnswerTextProps = {
  children: string;
};

// children은 html 형태의 string 으로 오기 때문에, 이를 변환하는 작업이 필요하다.
export const AnswerMainText = ({ children }: AnswerTextProps) => {
  return (
    <main>
      <div
        className="prose max-w-none"
        dangerouslySetInnerHTML={{ __html: Dompurify.sanitize(children) }}
      />
    </main>
  );
};
