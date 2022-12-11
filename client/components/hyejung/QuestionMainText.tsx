/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-29
 * 최근 수정일: 2022-11-29
 */
type MainTextProps = {
  children: string;
};

// 본문 텍스트
export const QuestionMainText = ({ children }: MainTextProps) => {
  return (
    <main className="flex min-h-[20vh]">
      <div
        className="prose max-w-full"
        dangerouslySetInnerHTML={{ __html: children }}
      />
    </main>
  );
};
