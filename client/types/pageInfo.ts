export interface PageInfo {
  page: number;
  size: number;
  totalElements: number;
  sort: { empty: boolean; unsorted: boolean; sorted: boolean };
}
