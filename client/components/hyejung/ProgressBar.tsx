type ProgressProps = {
  pageNumber: number;
};

export const ProgressBar = ({ pageNumber }: ProgressProps) => {
  return (
    <article className="hidden w-[250px] md:flex  flex-col h-full space-y-8">
      {[
        '채택 태그 선택',
        '후기 남기기',
        '모락 보내기 (선택)',
        '후기 전송!',
      ].map((text, i) =>
        i === pageNumber ? (
          <div className="flex items-center -ml-[4px]" key={text}>
            <div className="w-[6px] h-[6px] bg-main-orange rounded-full" />
            <span className="pl-3 font-bold">{text}</span>
          </div>
        ) : (
          <div className="flex items-center" key={text}>
            <span className="pl-[14px] text-main-gray">{text}</span>
          </div>
        ),
      )}
    </article>
  );
};
