export class TodoItemNode {
  children!: TodoItemNode[];
  label!: string;
  id!:number;
  isChecked!:boolean;
  isPlanType!: boolean;
  claimId!: number;
}

/** Flat to-do item node with expandable and level information */
export class TodoItemFlatNode {
  label!: string;
  level!: number;
  expandable!: boolean;
  id!: number;
  isChecked!: boolean;
  isPlanType!: boolean;
  claimId!: number;
}