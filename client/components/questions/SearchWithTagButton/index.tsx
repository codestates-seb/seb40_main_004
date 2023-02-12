import { SetterOrUpdater } from 'recoil';

const tags = [
  { tagId: 0, name: 'JAVA' },
  { tagId: 1, name: 'C' },
  { tagId: 2, name: 'NODE' },
  { tagId: 3, name: 'SPRING' },
  { tagId: 4, name: 'REACT' },
  { tagId: 5, name: 'JAVASCRIPT' },
  { tagId: 6, name: 'CPLUSPLUS' },
  { tagId: 7, name: 'CSHOP' },
  { tagId: 8, name: 'NEXT' },
  { tagId: 9, name: 'NEST' },
  { tagId: 10, name: 'PYTHON' },
  { tagId: 11, name: 'SWIFT' },
  { tagId: 12, name: 'KOTLIN' },
  { tagId: 13, name: 'CSS' },
  { tagId: 14, name: 'HTML' },
  { tagId: 15, name: 'AWS' },
  { tagId: 16, name: 'REDUX' },
  { tagId: 17, name: 'SCALA' },
  { tagId: 18, name: 'GO' },
  { tagId: 19, name: 'TYPESCRIPT' },
];

type KeyProps = {
  setKeyword: SetterOrUpdater<string>;
  setTarget: React.Dispatch<React.SetStateAction<string>>;
  keyword: string;
};

const getTagList = () => {
  const tagList: { tagId: number; name: string }[][] = [];
  let currentList: { tagId: number; name: string }[] = [];
  tags.forEach((tag, index) => {
    currentList.push(tag);
    if ((index + 1) % 3 === 0) {
      tagList.push(currentList);
      currentList = [];
    }
  });
  if (currentList.length > 0) {
    tagList.push(currentList);
  }
  return tagList;
};

export const SearchWithTagButton = ({
  setKeyword,
  setTarget,
  keyword,
}: KeyProps) => {
  const tagList = getTagList();
  return (
    <section className="flex flex-col mt-8 justify-center space-y-6">
      {tagList.map((tags, i) => (
        <div className="flex justify-center space-x-4" key={i}>
          {tags.map((tag) => (
            <div
              key={tag.tagId}
              className={`px-4 py-1 min-w-[80px] hover:bg-opacity-100 transition rounded-full text-center cursor-pointer ${
                keyword === tag.name
                  ? 'bg-main-orange bg-opacity-80'
                  : 'bg-main-yellow  bg-opacity-50'
              }`}
              onClick={() => {
                setKeyword(tag.name);
                setTarget('tag');
              }}
            >
              {tag.name}
            </div>
          ))}
        </div>
      ))}
    </section>
  );
};
