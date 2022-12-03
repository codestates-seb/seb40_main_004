export const changeTagPrettier = (tag: string) =>
  tag
    .split('')
    .map((char, i) => (i === 0 ? char : char.toLowerCase()))
    .join('');
